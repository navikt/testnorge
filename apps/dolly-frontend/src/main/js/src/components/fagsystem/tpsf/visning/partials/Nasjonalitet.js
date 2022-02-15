import React from 'react'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Innvandring } from '~/components/fagsystem/pdlf/visning/partials/Innvandring'
import { Utvandring } from '~/components/fagsystem/pdlf/visning/partials/Utvandring'

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

const InnvandretUtvandret = ({ data }) => {
	if (data.length < 1) return null
	return (
		<ErrorBoundary>
			<DollyFieldArray data={data} header={'Innvandret/utvandret'} nested>
				{(innvandringUtvandring) => (
					<>
						<TitleValue title="Status" value={innvandringUtvandring.innutvandret} />
						<TitleValue
							title="Land"
							value={innvandringUtvandring.landkode}
							kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
						/>
						<TitleValue
							title="Flyttedato"
							value={Formatters.formatDate(innvandringUtvandring.flyttedato)}
						/>
					</>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}

export const Nasjonalitet = ({ data, pdlData, visTittel = true }) => {
	const { statsborgerskap, sprakKode, innvandretUtvandret } = data
	const pdlInnvandret = pdlData?.innflytting
	const pdlUtvandret = pdlData?.utflytting

	if (!statsborgerskap) {
		return null
	}

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
					<Statsborgerskap statsborgerskap={statsborgerskap[0] || statsborgerskap} />
				)}
				{pdlInnvandret && <Innvandring data={pdlInnvandret} />}
				{pdlUtvandret && <Utvandring data={pdlUtvandret} />}
				{innvandretUtvandret && !pdlInnvandret && !pdlUtvandret && (
					<InnvandretUtvandret data={innvandretUtvandret} />
				)}
				<TitleValue title="Språk" kodeverk={PersoninformasjonKodeverk.Spraak} value={sprakKode} />
			</div>
		</div>
	)
}
