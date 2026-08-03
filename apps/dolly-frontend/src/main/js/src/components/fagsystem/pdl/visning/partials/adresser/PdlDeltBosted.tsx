import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DeltBostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { Adresse } from '@/components/fagsystem/pdlf/visning/partials/DeltBosted'
import { sortPdlItems } from '@/components/fagsystem/pdl/visning/partials/utils'

type PdlDeltBostedProps = {
	data: Array<DeltBostedData>
}

type AdresseProps = {
	data: DeltBostedData
	idx?: number
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse adresse={data} idx={idx} />
		</div>
	)
}

export const PdlDeltBosted = ({ data }: PdlDeltBostedProps) => {
	if (!data || data.length === 0) {
		return null
	}

	const gyldigeAdresser = sortPdlItems(
		data.filter((adresse: DeltBostedData) => !adresse.metadata?.historisk),
	)
	const historiskeAdresser = sortPdlItems(
		data.filter((adresse: DeltBostedData) => adresse.metadata?.historisk),
	)

	return (
		<>
			<SubOverskrift label="Delt bosted" iconKind="adresse" />
			<ArrayHistorikk
				component={AdresseVisning}
				data={gyldigeAdresser}
				historiskData={historiskeAdresser}
				header={''}
			/>
		</>
	)
}
