import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

type Data = {
	data: Array<any>
}

export const Oppholdsadresse = ({ data }: Data) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<SubOverskrift label="Oppholdsadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) => {
							return (
								<>
									{adresse.vegadresse && <Vegadresse adresse={adresse} idx={idx} />}
									{adresse.matrikkeladresse && <Matrikkeladresse adresse={adresse} idx={idx} />}
									{adresse.utenlandskAdresse && <UtenlandskAdresse adresse={adresse} idx={idx} />}
									{adresse.oppholdAnnetSted && (
										<div className="person-visning_content" key={idx}>
											<TitleValue
												title="Opphold annet sted"
												value={Formatters.showLabel('oppholdAnnetSted', adresse.oppholdAnnetSted)}
											/>
										</div>
									)}
								</>
							)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
