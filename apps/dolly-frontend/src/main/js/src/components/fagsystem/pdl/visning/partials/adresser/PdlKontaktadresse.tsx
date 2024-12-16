import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import {
	Adresse,
	KontaktadresseVisning,
} from '@/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { KontaktadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import * as _ from 'lodash-es'

type PdlKontaktadresseProps = {
	data: Array<KontaktadresseData>
	pdlfData?: Array<KontaktadresseData>
	tmpPersoner?: any
	ident?: string
	identtype?: string
}

type AdresseProps = {
	data: KontaktadresseData
	idx: number
	alleData?: Array<KontaktadresseData>
	tmpData?: any
	tmpPersoner?: any
	ident?: string
	identtype?: string
	master?: string
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse kontaktadresseData={data} idx={idx} />
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
			<KontaktadresseVisning
				kontaktadresseData={data}
				idx={idx}
				data={alleData}
				tmpData={tmpData}
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
				erPdlVisning={false}
				master={master}
			/>
		</div>
	)
}

export const PdlKontaktadresse = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	identtype,
}: PdlKontaktadresseProps) => {
	if ((!data || data.length === 0) && (!tmpPersoner || Object.keys(tmpPersoner).length < 1)) {
		return null
	}

	const tmpData = _.get(tmpPersoner, `${ident}.person.kontaktadresse`)
	if ((!data || data.length === 0) && (!tmpData || tmpData.length < 1)) {
		return null
	}

	const gyldigeAdresser = data.filter((adresse: KontaktadresseData) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter(
		(adresse: KontaktadresseData) => adresse.metadata?.historisk,
	)

	return (
		<>
			<SubOverskrift label="Kontaktadresse" iconKind="postadresse" />
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
