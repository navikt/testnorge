import Title from '../../components/title'
import './Endringsmelding.less'
import { LinkPanel } from '@navikt/ds-react'

export default () => {
	return (
		<div>
			<Title title={'Endringsmelding'} />
			<div className={'endringsmelding-tekst'}>
				<p>
					Endringsmelding er en egen applikasjon separat fra Dolly som brukes til å sende inn
					fødsels- og dødsmeldinger til ønsket testmiljø.
					<br />
					Tilgang til Endringsmelding er begrenset. Hvis man trenger tilgang kan man ta kontakt med
					Team Dolly.
					<br />
					<br />
					Trykk på knappen under for å gå direkte til Endringmelding.
				</p>
				<div className={'linkPanel-container'}>
					<LinkPanel
						className={'linkPanel'}
						target="_blank"
						href="https://testnav-endringsmelding.intern.dev.nav.no"
					>
						<LinkPanel.Title>Endringsmelding</LinkPanel.Title>
					</LinkPanel>
				</div>
			</div>
		</div>
	)
}
