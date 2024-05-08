import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { BostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { Adresse, BoadresseVisning } from '@/components/fagsystem/pdlf/visning/partials/Boadresse'

type PdlBoadresseProps = {
	data: Array<BostedData>
	pdlfData?: Array<BostedData>
	tmpPersoner?: any
	ident?: string
	identtype?: string
}

type AdresseProps = {
	data: BostedData
	idx?: number
	alleData?: Array<BostedData>
	tmpPersoner?: any
	ident?: string
	identtype?: string
	master?: string
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse boadresseData={data} idx={idx} />
		</div>
	)
}

const AdresseVisningRedigerbar = ({
	data,
	idx,
	alleData,
	tmpPersoner,
	ident,
	identtype,
	master,
}: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<BoadresseVisning
				boadresseData={data}
				idx={idx}
				data={alleData}
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
				erPdlVisning={false}
				master={master}
			/>
		</div>
	)
}

export const PdlBoadresse = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	identtype,
}: PdlBoadresseProps) => {
	if ((!data || data.length === 0) && (!tmpPersoner || Object.keys(tmpPersoner).length < 1)) {
		return null
	}

	const gyldigeAdresser = data.filter((adresse: BostedData) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter((adresse: BostedData) => adresse.metadata?.historisk)

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<ArrayHistorikk
				component={AdresseVisning}
				componentRedigerbar={AdresseVisningRedigerbar}
				data={gyldigeAdresser}
				pdlfData={pdlfData}
				historiskData={historiskeAdresser}
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
				header={''}
			/>
		</>
	)
}
