import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { SoekForm } from '@/pages/dollySoek/SoekForm'

export default () => {
	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Dolly" />
				<Hjelpetekst placement={bottom}>
					Her kan du søke etter personer i Dolly ut fra ulike søkekriterier. Slik kan du gjenbruke
					eksisterende personer til nye formål.
				</Hjelpetekst>
			</div>
			<SoekForm />
		</div>
	)
}
