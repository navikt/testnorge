import React from 'react'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

const Statsborgerskap = ({ statsborgerskap }) => {
	if (!statsborgerskap) {
		return null
	}
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

export const Nasjonalitet = ({ data, visTittel = true, pdlData }) => {
	const { statsborgerskap, sprakKode } = data
	const pdlPerson = pdlData?.[0]?.person

	if (!data && !pdlData) {
		return null
	}

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			{statsborgerskap && (
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
			)}

			{pdlPerson?.innflytting?.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={pdlPerson.innflytting} header={'Innvandret'} nested>
						{(id, idx) => (
							<>
								<TitleValue
									title="Fraflyttingsland"
									value={pdlPerson.innflytting[idx].fraflyttingsland}
									kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								/>
								<TitleValue
									title="Fraflyttingssted"
									value={pdlPerson.innflytting[idx].fraflyttingsstedIUtlandet}
								/>
								<TitleValue
									title="Innflyttingsdato"
									value={pdlPerson.innflytting[idx].innflyttingsdato}
								/>
							</>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}

			{pdlPerson?.utflytting?.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={pdlPerson.utflytting} header={'Utvandret'} nested>
						{(id, idx) => (
							<>
								<TitleValue
									title="Tilflyttingsland"
									value={pdlPerson.utflytting[idx].tilflyttingsland}
									kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								/>
								<TitleValue
									title="Tilflyttingssted"
									value={pdlPerson.utflytting[idx].tilflyttingsstedIUtlandet}
								/>
								<TitleValue
									title="Utflyttingsdato"
									value={pdlPerson.utflytting[idx].utflyttingsdato}
								/>
							</>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</div>
	)
}
