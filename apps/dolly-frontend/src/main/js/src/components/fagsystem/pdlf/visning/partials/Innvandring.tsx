import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { InnvandringValues } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type InnvandringData = {
	data: Array<InnvandringValues>
}

export const Innvandring = ({ data }: InnvandringData) => {
	if (data.length < 1) return null
	return (
		<ErrorBoundary>
			<DollyFieldArray data={data} header={'Innvandret'} nested>
				{(innvandring: InnvandringValues) => (
					<>
						<TitleValue
							title="Fraflyttingsland"
							value={innvandring.fraflyttingsland}
							kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
						/>
						<TitleValue title="Fraflyttingssted" value={innvandring.fraflyttingsstedIUtlandet} />
						<TitleValue
							title="Innflyttingsdato"
							value={Formatters.formatDate(innvandring.innflyttingsdato)}
						/>
					</>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
