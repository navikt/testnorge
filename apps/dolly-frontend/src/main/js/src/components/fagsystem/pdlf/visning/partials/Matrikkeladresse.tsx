import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { MatrikkelAdresse } from '@/components/adresseVelger/MatrikkelAdresseVelger'

interface MatrikkeladresseValues {
	adresse: {
		matrikkeladresse: MatrikkelAdresse
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

export const Matrikkeladresse = ({ adresse, idx }: MatrikkeladresseValues) => {
	const { kommunenummer, gaardsnummer, bruksnummer, postnummer, bruksenhetsnummer, tilleggsnavn } =
		adresse.matrikkeladresse
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
			<h4 style={{ marginTop: '0px' }}>Matrikkeladresse</h4>
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Gårdsnummer" value={gaardsnummer} />
				<TitleValue title="Bruksnummer" value={bruksnummer} />
				<TitleValue title="Bruksenhetsnummer" value={bruksenhetsnummer} />
				<TitleValue title="Tilleggsnavn" value={tilleggsnavn} />
				<TitleValue title="Postnummer">
					{postnummer && (
						<KodeverkConnector navn="Postnummer" value={postnummer}>
							{(_v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : postnummer}</span>
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
