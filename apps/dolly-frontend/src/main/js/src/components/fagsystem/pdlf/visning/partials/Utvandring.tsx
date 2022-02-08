import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { UtvandringValues } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type UtvandringData = {
	data: Array<UtvandringValues>
}

export const Utvandring = ({ data }: UtvandringData) => {
	if (data.length < 1) return null
	return (
		<ErrorBoundary>
			<DollyFieldArray data={data} header={'Utvandret'} nested>
				{(utvandring: UtvandringValues) => (
					<>
						<TitleValue
							title="Tilflyttingsland"
							value={utvandring.tilflyttingsland}
							kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
						/>
						<TitleValue title="Tilflyttingssted" value={utvandring.tilflyttingsstedIUtlandet} />
						<TitleValue
							title="Utflyttingsdato"
							value={Formatters.formatDate(utvandring.utflyttingsdato)}
						/>
					</>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
