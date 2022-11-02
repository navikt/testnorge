import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const Utenlandsopphold = ({ data }) => {
	if (!data) return null

	return (
		<React.Fragment>
			<h4>Utenlandsopphold</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div key={idx} className="person-visning_content">
							<TitleValue
								title="Land"
								value={id.landkode}
								kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
							/>
							{id.periode && (
								<TitleValue
									title="Startdato"
									value={Formatters.formatStringDates(id.periode.fom)}
								/>
							)}
							{id.periode && (
								<TitleValue
									title="Sluttdato"
									value={Formatters.formatStringDates(id.periode.tom)}
								/>
							)}
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
