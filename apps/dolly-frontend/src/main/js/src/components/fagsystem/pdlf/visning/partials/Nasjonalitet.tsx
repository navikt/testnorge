import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Innvandring } from '~/components/fagsystem/pdlf/visning/partials/Innvandring'
import { Utvandring } from '~/components/fagsystem/pdlf/visning/partials/Utvandring'
import _cloneDeep from 'lodash/cloneDeep'
import { initialStatsborgerskap } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarConnector'
import { PersonData, Statsborgerskap } from '~/components/fagsystem/pdlf/PdlTypes'

type NasjonalitetTypes = {
	data: PersonData
	tmpPersoner?: Array<PersonData>
	visTittel?: boolean
	erPdlVisning?: boolean
}

type StatsborgerskapTypes = {
	statsborgerskapData: Statsborgerskap
	idx: number
}

export const Nasjonalitet = ({
	data,
	tmpPersoner,
	visTittel = true,
	erPdlVisning = false,
}: NasjonalitetTypes) => {
	if (!data) return null

	const { statsborgerskap, innflytting, utflytting, ident } = data

	if (!statsborgerskap && !innflytting && !utflytting) return null
	if (statsborgerskap?.length < 1 && innflytting?.length < 1 && utflytting?.length < 1) return null

	const StatsborgerskapLes = ({ statsborgerskapData, idx }: StatsborgerskapTypes) => {
		if (statsborgerskapData) {
			return (
				<div className="person-visning_redigerbar" key={idx}>
					<TitleValue
						title="Statsborgerskap"
						kodeverk={AdresseKodeverk.StatsborgerskapLand}
						value={statsborgerskapData.landkode}
					/>
					<TitleValue
						title="Statsborgerskap registrert"
						value={Formatters.formatDate(statsborgerskapData.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Statsborgerskap til"
						value={Formatters.formatDate(statsborgerskapData.gyldigTilOgMed)}
					/>
				</div>
			)
		}
		return null
	}

	const StatsborgerskapVisning = ({ statsborgerskapData, idx }: StatsborgerskapTypes) => {
		const initStatsborgerskap = Object.assign(
			_cloneDeep(initialStatsborgerskap),
			data.statsborgerskap[idx]
		)
		const initialValues = { statsborgerskap: initStatsborgerskap }

		const redigertStatsborgerskapPdlf = _get(tmpPersoner, `${ident}.person.statsborgerskap`)?.find(
			(a: Statsborgerskap) => a.id === statsborgerskapData.id
		)
		const slettetStatsborgerskapPdlf =
			tmpPersoner?.hasOwnProperty(ident) && !redigertStatsborgerskapPdlf
		if (slettetStatsborgerskapPdlf) return <pre style={{ margin: '0' }}>Opplysning slettet</pre>

		const statsborgerskapValues = redigertStatsborgerskapPdlf
			? redigertStatsborgerskapPdlf
			: statsborgerskapData
		const redigertStatsborgerskapValues = redigertStatsborgerskapPdlf
			? {
					statsborgerskap: Object.assign(
						_cloneDeep(initialStatsborgerskap),
						redigertStatsborgerskapPdlf
					),
			  }
			: null
		return erPdlVisning ? (
			<StatsborgerskapLes statsborgerskapData={statsborgerskapData} idx={idx} />
		) : (
			<VisningRedigerbarConnector
				dataVisning={<StatsborgerskapLes statsborgerskapData={statsborgerskapValues} idx={idx} />}
				initialValues={initialValues}
				redigertAttributt={redigertStatsborgerskapValues}
				path="statsborgerskap"
				ident={ident}
			/>
		)
	}

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			{statsborgerskap?.length > 0 && (
				<div className="person-visning_content" style={{ marginTop: '-15px' }}>
					<ErrorBoundary>
						<DollyFieldArray data={data.statsborgerskap} header="Statsborgerskap" nested>
							{(borgerskap: Statsborgerskap, idx: number) => (
								<StatsborgerskapVisning statsborgerskapData={borgerskap} idx={idx} />
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</div>
			)}
			{innflytting?.length > 0 && <Innvandring data={innflytting} />}
			{utflytting?.length > 0 && <Utvandring data={utflytting} />}
		</div>
	)
}
