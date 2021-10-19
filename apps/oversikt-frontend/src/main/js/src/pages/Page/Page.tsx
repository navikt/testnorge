import React from 'react'
import { Header, HeaderLink, HeaderLinkGroup, ProfilLoader } from '@navikt/dolly-komponenter'
import { ProfilService } from '@/services'

type Props = {
	children?: React.ReactNode
	loggedIn?: boolean
}

const Page = ({ children, loggedIn = true }: Props) => (
	<>
		<Header title="Generer token" profile={loggedIn && <ProfilLoader {...ProfilService} />}>
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
