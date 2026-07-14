package com.example.pf_secondlife_client.application

import com.example.pf_secondlife_client.data.payments.PaymentsRepository
import com.example.pf_secondlife_client.data.payments.PaymentsRepositoryImpl
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.data.posts.PostsRepositoryImpl
import com.example.pf_secondlife_client.data.session.sessionManagers.SessionManager
import com.example.pf_secondlife_client.data.session.sessionManagers.SessionManagerImpl
import com.example.pf_secondlife_client.data.uploads.UploadsRepository
import com.example.pf_secondlife_client.data.uploads.UploadsRepositoryImpl
import com.example.pf_secondlife_client.data.users.UsersRepository
import com.example.pf_secondlife_client.data.users.UsersRepositoryImpl
import com.example.pf_secondlife_client.network.api.payments.PaymentsApi
import com.example.pf_secondlife_client.network.api.payments.PaymentsApiImpl
import com.example.pf_secondlife_client.network.api.posts.PostsApi
import com.example.pf_secondlife_client.network.api.posts.PostsApiImpl
import com.example.pf_secondlife_client.network.api.uploads.UploadsApi
import com.example.pf_secondlife_client.network.api.uploads.UploadsApiImpl
import com.example.pf_secondlife_client.network.api.users.UsersApi
import com.example.pf_secondlife_client.network.api.users.UsersApiImpl
import com.example.pf_secondlife_client.network.HttpClientFactory
import com.example.pf_secondlife_client.data.session.tokenProviders.DataStoreTokenProvider
import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.ui.imageManagement.bytesReaders.ImageBytesReader
import com.example.pf_secondlife_client.ui.imageManagement.bytesReaders.ImageBytesReaderImpl
import com.example.pf_secondlife_client.ui.catalog.CatalogViewModel
import com.example.pf_secondlife_client.ui.createPost.CreatePostFormViewModel
import com.example.pf_secondlife_client.ui.editPost.EditPostFormViewModel
import com.example.pf_secondlife_client.ui.payment.PaymentFormViewModel
import com.example.pf_secondlife_client.ui.postDetail.PostDetailViewModel
import com.example.pf_secondlife_client.ui.user.login.LoginFormViewModel
import com.example.pf_secondlife_client.ui.user.profile.ProfileViewModel
import com.example.pf_secondlife_client.ui.user.register.RegisterFormViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<TokenProvider> { DataStoreTokenProvider(androidApplication()) }
    single<HttpClient> { HttpClientFactory.create(get()) }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    single<SessionManager> { SessionManagerImpl(get(), get()) }

    single<UsersApi> { UsersApiImpl(get(), get()) }
    single<PostsApi> { PostsApiImpl(get(), get()) }
    single<PaymentsApi> { PaymentsApiImpl(get(), get()) }
    single<UploadsApi> { UploadsApiImpl(get(), get()) }

    single<ImageBytesReader> { ImageBytesReaderImpl(androidApplication()) }

    single<UsersRepository> { UsersRepositoryImpl(get(), get(), get()) }
    single<PostsRepository> { PostsRepositoryImpl(get()) }
    single<PaymentsRepository> { PaymentsRepositoryImpl(get()) }
    single<UploadsRepository> { UploadsRepositoryImpl(get()) }

    viewModel { LoginFormViewModel(get()) }
    viewModel { RegisterFormViewModel(get()) }
    viewModel { CatalogViewModel(get()) }
    viewModel { (postId: String) -> PostDetailViewModel(postId, get(), get()) }
    viewModel { (postId: String) -> PaymentFormViewModel(postId, get(), get()) }
    viewModel { (userId: String) -> ProfileViewModel(userId, get(), get()) }
    viewModel { CreatePostFormViewModel(get(), get(), get()) }
    viewModel { (postId: String) -> EditPostFormViewModel(postId, get(), get(), get()) }
}