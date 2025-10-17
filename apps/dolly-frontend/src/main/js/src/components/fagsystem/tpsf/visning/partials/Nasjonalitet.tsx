import { AdresseKodeverk } from '@/config/kodeverk'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

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
				value={formatDate(statsborgerskap.statsborgerskapRegdato)}
			/>
			<TitleValue
				title="Statsborgerskap til"
				value={formatDate(statsborgerskap.statsborgerskapTildato)}
			/>
		</div>
	)
}

const InnvandretUtvandret = ({ data }) => {
	if (!data || data?.length < 1) {
		return null
	}
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
						<TitleValue title="Flyttedato" value={formatDate(innvandringUtvandring.flyttedato)} />
					</>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}

export const TpsfNasjonalitet = ({ data, visTittel = true }) => {
	const { statsborgerskap, innvandretUtvandret } = data

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
							{(borgerskap) => <Statsborgerskap statsborgerskap={borgerskap} />}
						</DollyFieldArray>
					</ErrorBoundary>
				) : (
					<Statsborgerskap statsborgerskap={statsborgerskap[0] || statsborgerskap} />
				)}
				<InnvandretUtvandret data={innvandretUtvandret} />
			</div>
		</div>
	)
}
