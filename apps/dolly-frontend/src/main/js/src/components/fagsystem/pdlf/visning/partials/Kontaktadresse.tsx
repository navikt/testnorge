import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Kontaktadresse = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<SubOverskrift label="Kontaktadresse" iconKind="postadresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse, idx: number) => {
							return (
								<>
									{adresse.vegadresse && <Vegadresse adresse={adresse} idx={idx} />}
									{adresse.utenlandskAdresse && <UtenlandskAdresse adresse={adresse} idx={idx} />}
									{adresse.postboksadresse && (
										<>
											<h4 style={{ marginTop: '0px' }}>Postboksadresse</h4>
											<div className="person-visning_content" key={idx}>
												<TitleValue
													title="Postbokseier"
													value={adresse.postboksadresse.postbokseier}
												/>
												<TitleValue title="Postboks" value={adresse.postboksadresse.postboks} />
												<TitleValue title="Postnummer" value={adresse.postboksadresse.postnummer} />
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
