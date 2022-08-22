import React from 'react'
import styled from 'styled-components'
import { ProfilService } from '@/services'
import LogoutButton from '@/components/LogoutButton'
import { Header, HeaderLink, HeaderLinkGroup, ProfilLoader } from '@navikt/dolly-komponenter/lib'

type Props = {
	children?: React.ReactNode
	loggedIn?: boolean
}

const ProfileContainer = styled.div`
	display: flex;
	align-items: center;
`

const Profile = () => (
	<ProfileContainer>
		<LogoutButton
			onClick={() => {
				window.location.href = '/logout'
			}}
		/>
		<ProfilLoader {...ProfilService} />
	</ProfileContainer>
)

const Page = ({ children, loggedIn = true }: Props) => (
	<>
		<Header title="Generer token" profile={loggedIn && <Profile />}>
			{loggedIn && (
				<HeaderLinkGroup>
					<HeaderLink
						href="/magic-token"
						isActive={() =>
							window.location.pathname === '/' || window.location.pathname.includes('/magic-token')
						}
					>
						Magic Token
					</HeaderLink>
					<HeaderLink
						href="/access-token/dev-fss.dolly.dolly-backend"
						isActive={() => window.location.pathname.includes('/access-token')}
					>
						Access Token
					</HeaderLink>
					<HeaderLink href="/user" isActive={() => window.location.pathname.includes('/user')}>
						User
					</HeaderLink>
				</HeaderLinkGroup>
			)}
		</Header>
		{children}
	</>
)

export default Page
