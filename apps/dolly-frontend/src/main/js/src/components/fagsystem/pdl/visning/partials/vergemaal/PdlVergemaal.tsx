import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { VergemaalKodeverk } from '@/config/kodeverk'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import {
	TjenesteomraadeData,
	VergemaalData,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { Verge } from './Verge'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

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
					{data.vergeEllerFullmektig?.tjenesteomraade &&
						data.vergeEllerFullmektig.tjenesteomraade.length > 0 && (
							<DollyFieldArray
								data={data.vergeEllerFullmektig.tjenesteomraade}
								header="Tjenesteområde"
								nested
								whiteBackground
							>
								{(tjenesteomraade: TjenesteomraadeData, idx: number) => (
									<div className="person-visning_content" key={idx}>
										<TitleValue title="Tjenesteoppgave" value={tjenesteomraade.tjenesteoppgave} />
										<TitleValue
											title="Tjenestevirksomhet"
											value={tjenesteomraade.tjenestevirksomhet}
										/>
									</div>
								)}
							</DollyFieldArray>
						)}
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
