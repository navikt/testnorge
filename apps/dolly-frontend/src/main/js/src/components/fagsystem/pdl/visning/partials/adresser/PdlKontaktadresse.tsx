import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Adresse } from '@/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { KontaktadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'

type PdlKontaktadresseProps = {
	data: Array<KontaktadresseData>
}

type AdresseProps = {
	data: KontaktadresseData
	idx?: number
}

const AdresseVisning = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			<Adresse kontaktadresseData={data} idx={idx} />
		</div>
	)
}

export const PdlKontaktadresse = ({ data }: PdlKontaktadresseProps) => {
	if (!data || data.length === 0) {
		return null
	}

	const gyldigeAdresser = data.filter((adresse: KontaktadresseData) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter(
		(adresse: KontaktadresseData) => adresse.metadata?.historisk
	)

	return (
		<>
			<SubOverskrift label="Kontaktadresse" iconKind="postadresse" />
			<ArrayHistorikk
				component={AdresseVisning}
				data={gyldigeAdresser}
				historiskData={historiskeAdresser}
				header={''}
			/>
		</>
	)
}
