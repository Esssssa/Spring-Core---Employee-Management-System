# employee management system - spring core

plain spring app (no boot) using java config, component scanning, profiles, scopes and lifecycle callbacks

run `Main` for the dev profile or pass `prod` as a program argument to use the file repository
full console output is in `console-output.txt`

## singleton vs prototype

- singleton: EmployeeServiceImpl, both repositories, NotificationManager, the three notifiers, EmployeeValidator and CompanyProperties
  they have no per call state so one shared instance is enough and cheaper
- prototype: AuditLogger
  every audit entry should get its own logger so a new instance is created each time its requested
  main proves it by requesting it 3 times and getting instances #4 #5 #6 while `service == service2` is true
- spring doesnt call @PreDestroy on prototypes because it stops tracking them after creating them

## injection types

- constructor injection in EmployeeServiceImpl for the repository validator and notification manager
  they are required so they are final and can never be null
- constructor injection in NotificationManager for `List<Notifier>` (collection injection) and one notifier picked with `@Qualifier("smsNotifier")` for urgent messages
- setter injection for `ObjectProvider<AuditLogger>` in EmployeeServiceImpl
  its not needed to build the object so a setter is fine
- field injection with @Value for simple config values like raise.max-percentage and company.currency
  short and easy but harder to test so i only used it for config values
- @Primary on EmailNotifier so asking for a single Notifier returns email
- @Order 1 2 3 on email sms push so they always fire in that order

## the scoped bean problem

the service is a singleton so its dependencies are injected only once
if AuditLogger was injected directly the service would keep the same logger forever and the prototype scope would do nothing

i used `ObjectProvider<AuditLogger>` and call `getObject()` every time i need a logger so each call gets a new instance
i picked it over a scoped proxy because its explicit, you can see in the code exactly where a new instance is requested, while a proxy hides that behind a fake object

## dev vs prod profile

both repositories implement EmployeeRepository
- `@Profile("dev")` InMemoryEmployeeRepository keeps employees in a map, lost when the app stops
- `@Profile("prod")` FileBackedEmployeeRepository reads and writes `employees.csv`

main sets the profile before `refresh()` so only one repository bean exists
the service just asks for EmployeeRepository and never knows which one it got, no code change needed to switch
