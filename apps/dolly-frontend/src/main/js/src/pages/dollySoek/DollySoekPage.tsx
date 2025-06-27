import Title from '../../components/title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { SoekForm } from '@/pages/dollySoek/SoekForm'
import { SisteSoek, soekType } from '@/components/ui/soekForm/SisteSoek'
import { useEffect, useState } from 'react'

export default () => {
	const [lagreSoekRequest, setLagreSoekRequest] = useState({})
	console.log('lagreSoekRequest: ', lagreSoekRequest) //TODO - SLETT MEG

	useEffect(() => {
		return () => {
			//TODO: send lagreSoekRequest til backend for lagring
			console.log('Kom deg vekk!') //TODO - SLETT MEG
		}
	}, [])

	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Dolly" />
				<Hjelpetekst placement={bottom}>
					Her kan du søke etter personer i Dolly ut fra ulike søkekriterier. Slik kan du gjenbruke
					eksisterende personer til nye formål.
				</Hjelpetekst>
			</div>
			<SisteSoek soekType={soekType.dolly} />
			<SoekForm lagreSoekRequest={lagreSoekRequest} setLagreSoekRequest={setLagreSoekRequest} />
		</div>
	)
}
