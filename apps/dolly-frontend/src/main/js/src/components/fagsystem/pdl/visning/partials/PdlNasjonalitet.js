import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

const Statsborgerskap = ({ statsborgerskap }) => {
	if (statsborgerskap) {
		const land = statsborgerskap.land ? statsborgerskap.land : statsborgerskap.landkode
		return (
			<div className="person-visning_content">
				<TitleValue
					title="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					value={land}
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
		)
	}
	return null
}

export const PdlNasjonalitet = ({ data, visTittel = true }) => {
	const { statsborgerskap, innflyttingTilNorge, utflyttingFraNorge, innflytting, utflytting } = data
	if (
		statsborgerskap?.length < 1 &&
		innflyttingTilNorge?.length < 1 &&
		utflyttingFraNorge?.length < 1
	)
		return null

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
					<Statsborgerskap statsborgerskap={statsborgerskap?.[0]} />
				)}
			</div>

			{(innflyttingTilNorge?.length > 0 || innflytting?.length > 0) && (
				<ErrorBoundary>
					<DollyFieldArray data={innflyttingTilNorge || innflytting} header={'Innvandret'} nested>
						{(innvandring) => (
							<>
								<TitleValue
									title="Fraflyttingsland"
									value={innvandring.fraflyttingsland}
									kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								/>
								<TitleValue
									title="Fraflyttingssted"
									value={innvandring.fraflyttingsstedIUtlandet}
								/>
							</>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}

			{(utflyttingFraNorge?.length > 0 || utflytting?.length > 0) && (
				<ErrorBoundary>
					<DollyFieldArray data={utflyttingFraNorge || utflytting} header={'Utvandret'} nested>
						{(utvandring) => (
							<>
								<TitleValue
									title="Tilflyttingsland"
									value={utvandring.tilflyttingsland}
									kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								/>
								<TitleValue title="Tilflyttingssted" value={utvandring.tilflyttingsstedIUtlandet} />
							</>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</div>
	)
}
