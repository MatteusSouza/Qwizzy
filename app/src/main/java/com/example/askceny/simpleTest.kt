package com.example.askceny

import kotlinx.coroutines.runBlocking

/*
fun answerTest() {
    var exc = 0
    var excMsg = ""
    println("\nTest of class QuizRepositoryOld\nLog: ")

    try {
        val a = Answer("", "")
//        println("\t Not Passed: The field 'question' is empty.\n")
        exc +=1
    }catch (e: IllegalArgumentException) {
        println("\t$e \n\tPassed!\n")
    }


    excMsg = if (exc > 0)
        "Failed with $exc Exception(s)"
    else
        "Passed with no Exception(s)"
    println("\nEnd of initQuestionTest\n$excMsg\n")
}

fun questionTest() {
    var exc = 0
    var excMsg = ""
    println("\nTest of class QuestionOld\nLog: ")

    try {
        QuestionOld( question = "", answers = mutableListOf("A","B","C"), correctOption = 1)
        println("\t Not Passed: The field 'question' is empty.\n")
        exc +=1
    }catch (e: IllegalArgumentException) {
        println("\t$e \n\tPassed!\n")
    }

    try {
        QuestionOld( question = "Escolha uma opção ?", answers = mutableListOf(), correctOption = 1)
        println("\t Not Passed: answer field is an empty MutableList.\n")
        exc +=1
    }catch (e: IllegalArgumentException) {
        println("\t$e \n\tPassed!\n")
    }

    try {
        QuestionOld( question = "Escolha uma opção ?", answers = mutableListOf("A","B","C"), correctOption = -1)
        println("\t Not Passed: The field 'correctOption' accepted a negative value.\n")
        exc +=1
        QuestionOld( question = "Escolha uma opção ?", answers = mutableListOf("A","B","C"), correctOption = 0)
        println("\t Not Passed: The field 'correctOption' accepted a zero value.\n")
        exc +=1
    }catch (e: IllegalArgumentException) {
        println("\t$e \n\tPassed!\n")
    }

    try {
        QuestionOld( question = "Escolha uma opção ?", answers = mutableListOf("A","B","C"), correctOption = 4)
        println("\t Not Passed: The field 'correctOption' accepted a value greater than MutableList size.\n")
        exc +=1
    }catch (e: IllegalArgumentException) {
        println("\t$e \n\tPassed!\n")
    }

    excMsg = if (exc > 0)
        "Failed with $exc Exception(s)"
    else
        "Passed with no Exception(s)"
    println("\nEnd of initQuestionTest\n$excMsg\n")

}

*/
/*
fun quizTest() {
    var exc = 0
    var excMsg = ""
    println("\nTest of class QuizRepositoryOld\nLog: ")

    var q: QuizRepositoryOld? = null

    try {
        val q = QuizRepositoryOld();
//        QuestionOld( question = "", answers = mutableListOf("A","B","C"), correctOption = 1)
//        println("\t Not Passed: The field 'question' is empty.\n")
        exc +=1
    }catch (e: IllegalArgumentException) {
        println("\t$e \n\tPassed!\n")
    }


    excMsg = if (exc > 0)
        "Failed with $exc Exception(s)"
    else
        "Passed with no Exception(s)"
    println("\nEnd of initQuestionTest\n$excMsg\n")
}

fun userRepositoryTest() {
    val repo = AuthRepositoryFake()

    repo.createUser(username = "abcd", email = "abcd@exemplo.com", "", "")
    repo.createUser("efgh","efgh@exemplo.com", "", "")

    println(repo.getUserByEmail("abcd@exemplo.com"))
    println(repo.getUserByEmail("efgh@exemplo.com"))


}

fun dataTest() {
    mock.createUser(username = "abcd", email = "abcd@exemplo.com", "", "")
    mock.createUser("efgh","efgh@exemplo.com", "", "")
    mock.createUser("ijlm","ijlm@exemplo.com", "", "")
    mock.createUser("nopq","nopq@exemplo.com", "", "")

    println(mock.getUser("efgh@exemplo.com"))
}
*/

/*
fun userRepositoryTest() {
    val repo = AuthRepositoryFake()

    println(repo.login("testUser@test.com","test123"))

    repo.signUpWithEmail("testUser@test.com","test","test123")
    println(repo.getLoggedUser())

    println(repo.logoff())
    println(repo.getLoggedUser())

    println(repo.login("testUser@test.com","test123"))
    println(repo.getLoggedUser())
}
fun quizTest() {
    val token = MockServerApi.createUser(
        email = "abcd@exemplo.com",
        "abcd",
        "abc123467"
    )
    val isAuth : Boolean = MockServerApi.isAuthenticated(token)
    println(isAuth)

    MockServerApi.createQuiz("title1", "description1", img="", isPublic = false, token!!)
    MockServerApi.createQuiz("title2", "description2", img="", isPublic = false, token)

    val qzz = MockServerApi.getAllQuizzes(token)
    println("All Quizzes: $qzz")

    val q1 : String = qzz!![0].id
    MockServerApi.createQuestion(token, qzz[0].id, "Question 1", QuestionType.MULTIPLE_CHOICE_MULTIPLE)
    MockServerApi.createQuestion(token, qzz[0].id, "Question 2", QuestionType.MULTIPLE_CHOICE_MULTIPLE)
    MockServerApi.createQuestion(token, qzz[0].id, "Question 3", QuestionType.MULTIPLE_CHOICE_MULTIPLE)

    val allQuestions : MutableList<Question> = MockServerApi.getQuizAllQuestions(token, qzz[0].id) ?: mutableListOf()
    println("All Questions of first Quiz: $allQuestions")

    println("removing first question")
    MockServerApi.deleteQuestion(token, allQuestions[0].id)
    println(allQuestions)

    println("removing first Quiz")
    println("All Quizzes: $qzz")

    //    println(MockServerApi.deleteQuiz(token, qzz[0].id))
//    val questions = MockServerApi.getQuizAllQuestions(q1)
//    println("All Questions: $questions")

//    questions?.get(0)?.addAnswer("Answer A", "")
//    questions?.get(0)?.addAnswer("Answer B", "")
//    questions?.get(0)?.addAnswer("Answer C", "")
//    questions?.get(0)?.addAnswer("Answer D", "")

//    println(questions?.get(0)?.answers)
}

 */


fun main() {
//    quizTest()
//    userRepositoryTest()
//    questionTest()
//    quizTest()

}


