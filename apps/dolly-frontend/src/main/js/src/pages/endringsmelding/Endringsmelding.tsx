import React from 'react'
import Title from '~/components/Title'
import Logger from '~/logger'
import './Endringsmelding.less'

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
				<a
					href="https://endringsmelding.dev.intern.nav.no"
					target="_blank"
					onClick={() => Logger.log({ event: 'Trykket på endringsmelding knapp' })}
				>
					Endringsmelding
				</a>
			</div>
		</div>
	)
}
