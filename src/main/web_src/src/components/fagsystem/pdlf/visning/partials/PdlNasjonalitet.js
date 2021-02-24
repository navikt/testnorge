import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

const Statsborgerskap = ({ statsborgerskap }) =>
	statsborgerskap ? (
		<div className="person-visning_content">
			<TitleValue
				title="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				value={statsborgerskap.land}
			/>
			<TitleValue
				title="Statsborgerskap registrert"
				value={Formatters.formatDate(statsborgerskap.gyldigFraOgMed)}
			/>
			<TitleValue
				title="Statsborgerskap til"
				value={Formatters.formatDate(statsborgerskap.gyldigTilOgMed)}
			/>
		</div>
	) : null

export const PdlNasjonalitet = ({ data, visTittel = true }) => {
	const { statsborgerskap, innflyttingTilNorge } = data
	if (statsborgerskap?.length < 1 && innflyttingTilNorge?.length < 1) return null

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			<div className="person-visning_content">
				{statsborgerskap?.length > 1 ? (
					<ErrorBoundary>
						<DollyFieldArray data={statsborgerskap} header="Statsborgerskap" nested>
							{(borgerskap, idx) => <Statsborgerskap key={idx} statsborgerskap={borgerskap} />}
						</DollyFieldArray>
					</ErrorBoundary>
				) : (
					<Statsborgerskap statsborgerskap={statsborgerskap[0]} />
				)}
			</div>

			{innflyttingTilNorge?.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={innflyttingTilNorge} header={'Innvandret/utvandret'} nested>
						{(id, idx) => (
							<TitleValue
								title="Fraflyttingsland"
								value={innflyttingTilNorge[idx].fraflyttingsland}
							/>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</div>
	)
}
