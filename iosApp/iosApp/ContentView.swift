import SwiftUI
import UIKit
import ComposeApp

struct ContentView: View {
    var body: some View {
        ComposeAppView()
            .ignoresSafeArea()
    }
}

#Preview {
    ContentView()
}

private struct ComposeAppView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
