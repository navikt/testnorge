import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { VergemaalKodeverk } from '@/config/kodeverk'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { VergemaalData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { Verge } from './Verge'

type VergemaalProps = {
	data: Array<VergemaalData>
}

type VisningData = {
	data: VergemaalData
}

export const Visning = ({ data }: VisningData) => {
	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue
						title="Fylkesembete"
						kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
						value={data.embete}
					/>
					<TitleValue title="Sakstype" value={showLabel('pdlVergemaalType', data.type)} />
					<TitleValue
						title="Mandattype"
						value={showLabel('pdlVergemaalOmfang', data.vergeEllerFullmektig?.omfang)}
					/>
					<TitleValue
						title="Gyldig f.o.m."
						value={formatDate(data.folkeregistermetadata?.gyldighetstidspunkt)}
					/>
					<TitleValue
						title="Gyldig t.o.m."
						value={formatDate(data.folkeregistermetadata?.opphoerstidspunkt)}
					/>
					<Verge data={data.vergeEllerFullmektig} type={data.type} />
				</div>
			</ErrorBoundary>
		</>
	)
}

export const PdlVergemaal = ({ data }: VergemaalProps) => {
	if (!data || data.length < 1) {
		return null
	}

	const gyldigeVergemaal = data.filter((vergemaal: VergemaalData) => !vergemaal.metadata?.historisk)
	const historiskeVergemaal = data.filter(
		(vergemaal: VergemaalData) => vergemaal.metadata?.historisk,
	)

	return (
		<div>
			<SubOverskrift label="Vergemål" iconKind="vergemaal" />
			<ArrayHistorikk
				component={Visning}
				data={gyldigeVergemaal}
				historiskData={historiskeVergemaal}
				header={''}
			/>
		</div>
	)
}
