import { InternalHeader, Spacer } from '@navikt/ds-react'

export default () => {
	return (
		<InternalHeader>
			<InternalHeader.Title href="/">Dolly status</InternalHeader.Title>
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
