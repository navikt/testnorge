import { AdresseKodeverk } from '@/config/kodeverk'
import { formatStringDates } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'

export const Utenlandsopphold = ({ data }) => {
	if (!data) {
		return null
	}

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
								<TitleValue title="Startdato" value={formatStringDates(id.periode.fom)} />
							)}
							{id.periode && (
								<TitleValue title="Sluttdato" value={formatStringDates(id.periode.tom)} />
							)}
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
