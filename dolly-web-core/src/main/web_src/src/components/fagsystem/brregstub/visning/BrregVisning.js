import React from 'react'
import _get from 'lodash/get'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const BrregVisning = ({ data, bestillinger, loading }) => {
	if (loading) return <Loading label="laster brreg-data" />
	if (!data) return false

	const brregBestillinger = bestillinger.filter(bestilling =>
		bestilling.hasOwnProperty('brregstub')
	)
	const getPersonroller = idx => {
		return _get(brregBestillinger[0], `brregstub.enheter[${idx}].personroller`)
	}

	return (
		<div>
			<SubOverskrift label="Brønnøysundregistrene" iconKind="brreg" />
			<div className="person-visning_content">
				<TitleValue
					title="Understatuser"
					value={data.understatuser && data.understatuser.join(', ')}
				/>
				<DollyFieldArray data={data.enheter} header="Enhet">
					{(enhet, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Rolle" value={enhet.rollebeskrivelse} />
							<TitleValue
								title="Registreringsdato"
								value={Formatters.formatStringDates(enhet.registreringsdato)}
							/>
							<TitleValue title="Organisasjonsnummer" value={enhet.orgNr} />
							<TitleValue title="Foretaksnavn" value={enhet.foretaksNavn.navn1} />
							{/* Vi kan foreløpig ikke lese tilbake personroller pr. person, viser derfor bestillingsinfo */}
							{brregBestillinger.length === 1 && getPersonroller(idx).length > 0 && (
								<DollyFieldArray data={getPersonroller(idx)} header="Personroller" nested>
									{(personrolle, idx) => (
										<div className="person-visning_content" key={idx}>
											<TitleValue title="Egenskap" value={personrolle.egenskap} />
											<TitleValue
												title="Registreringsdato"
												value={Formatters.formatDate(personrolle.registringsDato)}
											/>
											<TitleValue
												title="Har fratrådt"
												value={Formatters.oversettBoolean(personrolle.fratraadt)}
											/>
										</div>
									)}
								</DollyFieldArray>
							)}
						</div>
					)}
				</DollyFieldArray>
			</div>
		</div>
	)
}
