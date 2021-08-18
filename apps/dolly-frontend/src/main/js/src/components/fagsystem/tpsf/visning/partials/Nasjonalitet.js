import React from 'react'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

const Statsborgerskap = ({ statsborgerskap }) => {
	return (
		<div className="person-visning_content">
			<TitleValue
				title="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				value={statsborgerskap.statsborgerskap}
			/>
			<TitleValue
				title="Statsborgerskap registrert"
				value={Formatters.formatDate(statsborgerskap.statsborgerskapRegdato)}
			/>
			<TitleValue
				title="Statsborgerskap til"
				value={Formatters.formatDate(statsborgerskap.statsborgerskapTildato)}
			/>
		</div>
	)
}

export const Nasjonalitet = ({ data, visTittel = true }) => {
	const { statsborgerskap, sprakKode, innvandretUtvandret } = data

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			<div className="person-visning_content">
				{statsborgerskap.length > 1 ? (
					<ErrorBoundary>
						<DollyFieldArray data={statsborgerskap} header="Statsborgerskap" nested>
							{(statsborgerskap, idx) => <Statsborgerskap statsborgerskap={statsborgerskap} />}
						</DollyFieldArray>
					</ErrorBoundary>
				) : (
					<Statsborgerskap statsborgerskap={statsborgerskap[0]} />
				)}
				<TitleValue title="Språk" kodeverk={PersoninformasjonKodeverk.Spraak} value={sprakKode} />
			</div>

			{innvandretUtvandret.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={innvandretUtvandret} header={'Innvandret/utvandret'} nested>
						{(id, idx) => (
							<React.Fragment>
								{innvandretUtvandret && (
									<>
										<TitleValue
											title={
												innvandretUtvandret[idx].innutvandret === 'UTVANDRET'
													? 'Utvandret til'
													: 'Innvandret fra'
											}
											kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
											value={innvandretUtvandret[idx].landkode}
										/>
										<TitleValue
											title="Flyttedato"
											value={Formatters.formatDate(innvandretUtvandret[idx].flyttedato)}
										/>
									</>
								)}
							</React.Fragment>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</div>
	)
}
