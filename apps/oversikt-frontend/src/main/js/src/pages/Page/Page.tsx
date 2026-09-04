import React, { useEffect, useState } from 'react'
import { ProfilService } from '@/services'
import {
	ActionMenu,
	BodyShort,
	Hide,
	HStack,
	InternalHeader,
	Loader,
	Page as AkselPage,
	Spacer,
	Theme,
} from '@navikt/ds-react'
import { LeaveIcon } from '@navikt/aksel-icons'
import { Link as RouterLink, useLocation } from 'react-router'
import { NavLogo } from '@/components/NavLogo/NavLogo'
import './Page.less'

type Props = {
	children?: React.ReactNode
	loggedIn?: boolean
}

const Profile = () => {
	const [profileName, setProfileName] = useState<string | null>(null)
	const [loading, setLoading] = useState(true)

	useEffect(() => {
		let isMounted = true

		ProfilService.fetchProfil()
			.then(({ visningsNavn }) => {
				if (isMounted) {
					setProfileName(visningsNavn)
				}
			})
			.catch(() => {
				if (isMounted) {
					setProfileName('Profil utilgjengelig')
				}
			})
			.finally(() => {
				if (isMounted) {
					setLoading(false)
				}
			})

		return () => {
			isMounted = false
		}
	}, [])

	return (
		<>
			{loading ? (
				<Loader size="small" title="Laster profil" variant="inverted" />
			) : (
				<ActionMenu>
					<ActionMenu.Trigger>
						<InternalHeader.UserButton name={profileName ?? 'Profil utilgjengelig'} />
					</ActionMenu.Trigger>
					<Theme theme="light">
						<ActionMenu.Content align="end">
							<ActionMenu.Label>
								<BodyShort size="small">{profileName ?? 'Profil utilgjengelig'}</BodyShort>
							</ActionMenu.Label>
							<ActionMenu.Divider />
							<ActionMenu.Group aria-label="Brukerhandlinger">
								<ActionMenu.Item as="a" href="/logout">
									Logg ut
									<Spacer />
									<LeaveIcon aria-hidden fontSize="1.5rem" />
								</ActionMenu.Item>
							</ActionMenu.Group>
						</ActionMenu.Content>
					</Theme>
				</ActionMenu>
			)}
		</>
	)
}

const navigationItems = [
	{
		href: '/magic-token',
		label: 'Magic Token',
		isActive: (pathname: string) => pathname === '/' || pathname.startsWith('/magic-token'),
	},
	{
		href: '/access-token/dev-gcp.dolly.dolly-backend',
		label: 'Access Token',
		isActive: (pathname: string) => pathname.startsWith('/access-token'),
	},
	{
		href: '/user',
		label: 'User',
		isActive: (pathname: string) => pathname.startsWith('/user'),
	},
]

const Page = ({ children, loggedIn = true }: Props) => {
	const { pathname } = useLocation()

	return (
		<AkselPage className="oversikt-page" contentBlockPadding="none">
			<InternalHeader className="oversikt-page__header">
				<InternalHeader.Title
					as={RouterLink}
					aria-label="NAV – Generer token"
					className="oversikt-page__brand"
					to="/magic-token"
				>
					<HStack align="center" gap="space-16">
						<NavLogo aria-hidden className="oversikt-page__logo" />
						<Hide below="lg">Generer token</Hide>
					</HStack>
				</InternalHeader.Title>
				{loggedIn && (
					<>
						<HStack
							as="nav"
							align="stretch"
							aria-label="Hovednavigasjon"
							className="oversikt-page__navigation"
						>
							{navigationItems.map((item) => {
								const active = item.isActive(pathname)

								return (
									<InternalHeader.Button
										key={item.href}
										as={RouterLink}
										aria-current={active ? 'page' : undefined}
										className="oversikt-page__navigation-button"
										isActive={active}
										to={item.href}
									>
										{item.label}
									</InternalHeader.Button>
								)
							})}
						</HStack>
						<HStack align="center" className="oversikt-page__profile" justify="end">
							<Profile />
						</HStack>
					</>
				)}
			</InternalHeader>
			<AkselPage.Block as="main" className="oversikt-page__content" gutters width="lg">
				{children}
			</AkselPage.Block>
		</AkselPage>
	)
}

export default Page
