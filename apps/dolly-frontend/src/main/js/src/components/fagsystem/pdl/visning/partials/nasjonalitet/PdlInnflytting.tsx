import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { InnflyttingTilNorge } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { formatDate } from '@/utils/DataFormatter'
import { sortPdlItems } from '@/components/fagsystem/pdl/visning/partials/utils'

type InnflyttingProps = {
	data: InnflyttingTilNorge
	idx?: number
}

type VisningProps = {
	innflytting: [InnflyttingTilNorge]
}

const Innflytting = ({ data, idx }: InnflyttingProps) => {
	if (data) {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Fraflyttingsland"
					value={data.fraflyttingsland}
					kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				/>
				<TitleValue title="Fraflyttingssted" value={data.fraflyttingsstedIUtlandet} />
				<TitleValue
					title="Innflyttingsdato"
					value={formatDate(data.folkeregistermetadata?.gyldighetstidspunkt)}
				/>
			</div>
		)
	}
	return null
}

export const PdlInnflytting = ({ innflytting }: VisningProps) => {
	if (innflytting?.length < 1) {
		return null
	}

	const gyldigeInnflyttinger = sortPdlItems(
		innflytting?.filter((flytting: InnflyttingTilNorge) => !flytting.metadata?.historisk) ?? [],
	)
	const historiskeInnflyttinger = sortPdlItems(
		innflytting?.filter((flytting: InnflyttingTilNorge) => flytting.metadata?.historisk) ?? [],
	)

	return (
		<div style={{ marginTop: '-10px' }}>
			<ArrayHistorikk
				component={Innflytting}
				data={gyldigeInnflyttinger}
				historiskData={historiskeInnflyttinger}
				header="Innvandret"
			/>
		</div>
	)
}
