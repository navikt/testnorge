import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import {
	Adresse,
	OppholdsadresseVisning,
} from '@/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { OppholdsadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import * as _ from 'lodash-es'

type PdlOppholdsadresseProps = {
	data: Array<OppholdsadresseData>
	pdlfData?: Array<OppholdsadresseData>
	tmpPersoner?: any
	ident?: string
	identtype?: string
}

type AdresseProps = {
	data: OppholdsadresseData
	idx?: number
	alleData?: Array<OppholdsadresseData>
	tmpData?: any
	tmpPersoner?: any
	ident?: string
	identtype?: string
	master?: string
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse oppholdsadresseData={data} idx={idx} />
		</div>
	)
}

const AdresseVisningRedigerbar = ({
	data,
	idx,
	alleData,
	tmpData,
	tmpPersoner,
	ident,
	identtype,
	master,
}: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<OppholdsadresseVisning
				oppholdsadresseData={data}
				idx={idx}
				data={alleData}
				tmpData={tmpData}
				tmpPersoner={tmpPersoner}
				ident={ident}
				erPdlVisning={false}
				identtype={identtype}
				master={master}
			/>
		</div>
	)
}

export const PdlOppholdsadresse = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	identtype,
}: PdlOppholdsadresseProps) => {
	if ((!data || data.length === 0) && (!tmpPersoner || Object.keys(tmpPersoner).length < 1)) {
		return null
	}

	const tmpData = _.get(tmpPersoner, `${ident}.person.oppholdsadresse`)
	if ((!data || data.length === 0) && (!tmpData || tmpData.length < 1)) {
		return null
	}

	const gyldigeAdresser = data.filter(
		(adresse: OppholdsadresseData) => !adresse.metadata?.historisk,
	)
	const historiskeAdresser = data.filter(
		(adresse: OppholdsadresseData) => adresse.metadata?.historisk,
	)

	return (
		<>
			<SubOverskrift label="Oppholdsadresse" iconKind="adresse" />
			<ArrayHistorikk
				component={AdresseVisning}
				componentRedigerbar={AdresseVisningRedigerbar}
				data={gyldigeAdresser}
				pdlfData={pdlfData}
				historiskData={historiskeAdresser}
				tmpData={tmpData}
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
				header={''}
			/>
		</>
	)
}
