import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type AdresseData = {
	adresse: {
		angittFlyttedato?: string
		gyldigFraOgMed?: string
		gyldigTilOgMed?: string
		utenlandskAdresse: {
			adressenavnNummer?: string
			postboksNummerNavn?: string
			postkode?: string
			bySted?: string
			landkode?: string
			bygningEtasjeLeilighet?: string
			regionDistriktOmraade?: string
		}
		oppholdAnnetSted?: string
		coAdressenavn?: string
		metadata: any
	}
	idx: number
}

export const UtenlandskAdresse = ({ adresse, idx }: AdresseData) => {
	const {
		adressenavnNummer,
		postboksNummerNavn,
		postkode,
		bySted,
		landkode,
		bygningEtasjeLeilighet,
		regionDistriktOmraade,
	} = adresse.utenlandskAdresse
	const {
		angittFlyttedato,
		gyldigFraOgMed,
		gyldigTilOgMed,
		oppholdAnnetSted,
		coAdressenavn,
		metadata,
	} = adresse

	const master = metadata?.master

	return (
		<>
			<h4 style={{ marginTop: '0px' }}>Utenlandsk adresse</h4>
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Gatenavn og husnummer" value={adressenavnNummer} />
				<TitleValue title="Postnummer og -navn" value={postboksNummerNavn} />
				<TitleValue title="Postkode" value={postkode} />
				<TitleValue title="By eller sted" value={bySted} />
				<TitleValue title="Land">
					{landkode && (
						<KodeverkConnector navn="Landkoder" value={landkode}>
							{(_v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : landkode}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Bygg-/leilighetsinfo" value={bygningEtasjeLeilighet} />
				<TitleValue title="Region/distrikt/område" value={regionDistriktOmraade} />
				<TitleValue title="Angitt flyttedato" value={formatDate(angittFlyttedato)} />
				<TitleValue title="Gyldig fra og med" value={formatDate(gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={formatDate(gyldigTilOgMed)} />
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
