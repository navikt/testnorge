import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'

type AdresseData = {
	adresse: {
		angittFlyttedato?: string
		gyldigFraOgMed?: string
		gyldigTilOgMed?: string
		startdatoForKontrakt?: string
		sluttdatoForKontrakt?: string
		ukjentBosted: {
			bostedskommune?: string
		}
		coAdressenavn?: string
	}
	idx: number
}

export const UkjentBosted = ({ adresse, idx }: AdresseData) => {
	const {
		angittFlyttedato,
		gyldigFraOgMed,
		gyldigTilOgMed,
		startdatoForKontrakt,
		sluttdatoForKontrakt,
		coAdressenavn,
	} = adresse
	return (
		<>
			<h4 style={{ marginTop: '0px' }}>Ukjent bosted</h4>
			<div className="person-visning_content" key={idx}>
				<TitleValue
					title="Bostedskommune"
					value={adresse.ukjentBosted.bostedskommune || 'Ikke oppgitt'}
				/>
				<TitleValue title="Angitt flyttedato" value={formatDate(angittFlyttedato)} />
				<TitleValue title="Gyldig fra og med" value={formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={formatDate(gyldigTilOgMed)} />
				<TitleValue title="Startdato for kontrakt" value={formatDate(startdatoForKontrakt)} />
				<TitleValue title="Sluttdato for kontrakt" value={formatDate(sluttdatoForKontrakt)} />
				<TitleValue title="C/O adressenavn" value={coAdressenavn} />
			</div>
		</>
	)
}
