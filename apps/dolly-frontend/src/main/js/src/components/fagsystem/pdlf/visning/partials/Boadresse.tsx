import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Boadresse = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse, idx: number) => {
							return (
								<>
									{adresse.vegadresse && <Vegadresse adresse={adresse} idx={idx} />}
									{adresse.matrikkeladresse && <Matrikkeladresse adresse={adresse} idx={idx} />}
									{adresse.utenlandskAdresse && <UtenlandskAdresse adresse={adresse} idx={idx} />}
									{adresse.ukjentBosted && (
										<>
											<h4 style={{ marginTop: '0px' }}>Ukjent bosted</h4>
											<div className="person-visning_content" key={idx}>
												<TitleValue
													title="Bostedskommune"
													value={adresse.ukjentBosted.bostedskommune || 'Ikke oppgitt'}
												/>
											</div>
										</>
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
