import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { Adresse } from '@/components/adresseVelger/AdresseVelger'

interface VegadresseValues {
	adresse: {
		vegadresse: Adresse
		angittFlyttedato: string
		gyldigFraOgMed?: string
		gyldigTilOgMed?: string
		startdatoForKontrakt?: string
		sluttdatoForKontrakt?: string
		oppholdAnnetSted?: string
		coAdressenavn?: string
		metadata: any
		master?: string
	}
	idx: number
}

export const Vegadresse = ({ adresse, idx }: VegadresseValues) => {
	const {
		adressenavn,
		tilleggsnavn,
		bruksenhetsnummer,
		husbokstav,
		husnummer,
		kommunenummer,
		postnummer,
		bydelsnummer,
	} = adresse.vegadresse
	const {
		angittFlyttedato,
		gyldigFraOgMed,
		gyldigTilOgMed,
		startdatoForKontrakt,
		sluttdatoForKontrakt,
		oppholdAnnetSted,
		coAdressenavn,
		metadata,
	} = adresse
	const master = adresse.master || metadata?.master

	return (
		<>
			<h4 style={{ marginTop: '0px' }}>Vegadresse</h4>
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Adressenavn" value={adressenavn} />
				<TitleValue title="Tilleggsnavn" value={tilleggsnavn} />
				<TitleValue title="Husnummer" value={husnummer} />
				<TitleValue title="Husbokstav" value={husbokstav} />
				<TitleValue title="Bruksenhetsnummer" value={bruksenhetsnummer} />
				<TitleValue title="Postnummer">
					{postnummer && (
						<KodeverkConnector navn="Postnummer" value={postnummer}>
							{(_v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : postnummer}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Bydelsnummer">
					{bydelsnummer && (
						<KodeverkConnector navn="Bydeler" value={bydelsnummer}>
							{(_v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : bydelsnummer}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Kommunenummer">
					{kommunenummer && (
						<KodeverkConnector navn="KommunerMedHistoriske" value={kommunenummer}>
							{(_v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : kommunenummer}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Angitt flyttedato" value={formatDate(angittFlyttedato)} />
				<TitleValue title="Gyldig fra og med" value={formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={formatDate(gyldigTilOgMed)} />
				<TitleValue title="Startdato for kontrakt" value={formatDate(startdatoForKontrakt)} />
				<TitleValue title="Sluttdato for kontrakt" value={formatDate(sluttdatoForKontrakt)} />
				<TitleValue
					title="Opphold annet sted"
					value={showLabel('oppholdAnnetSted', oppholdAnnetSted)}
				/>
				<TitleValue title="C/O adressenavn" value={coAdressenavn} />
				<TitleValue title="Master" value={master} />
			</div>
		</>
	)
}
