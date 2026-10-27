import DiscardableResult
import Testing

@Test
func testDiscardableFunctionsCanBeCalledAsStatements() throws {
    calls = 0

    discardable()
    #expect(calls == 1)

    let _ = checkable()
    #expect(calls == 2)
}

@Test
func testOverrides() throws {
    calls = 0

    SilentGreeter.shared.greet(name: "John")
    #expect(calls == 1)

    #expect(LoudGreeter().greet(name: "John") == "HELLO, John")
    #expect(calls == 2)

    let greeter: any Greeter = SilentGreeter.shared
    let _ = greeter.greet(name: "John")
    #expect(calls == 3)
}

@Test
func testMembers() throws {
    let counter = Counter()
    counter.increment()
    counter.increment()
    #expect(counter.value == 2)

    let one = Counter()
    one.increment()
    counter + one
    #expect((counter + one).value == 3)
}
