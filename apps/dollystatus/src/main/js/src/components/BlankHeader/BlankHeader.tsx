import { Hide, HStack, InternalHeader, Spacer } from '@navikt/ds-react'
import navLogo from '../../assets/nav-logo.svg'

export default () => {
	return (
		<InternalHeader>
			<InternalHeader.Title aria-label="NAV – Dolly status" href="/">
				<HStack align="center" gap="space-16">
					<img alt="" height={20} src={navLogo} width={64} />
					<Hide below="md">Dolly status</Hide>
				</HStack>
			</InternalHeader.Title>
			<Spacer />
			<InternalHeader.Button
				as="a"
				href="https://navikt.github.io/testnorge/"
				target="_blank"
				rel="noreferrer noopener"
			>
					Dokumentasjon
			</InternalHeader.Button>
		</InternalHeader>
	)
}
