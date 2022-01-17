import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type Data = {
	data: Array<any>
}

export const Kontaktadresse = ({ data }: Data) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<SubOverskrift label="Kontaktadresse" iconKind="postadresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) => {
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
												<TitleValue title="Postnummer">
													{adresse.postboksadresse.postnummer && (
														<KodeverkConnector
															navn="Postnummer"
															value={adresse.postboksadresse.postnummer}
														>
															{(v: Kodeverk, verdi: KodeverkValues) => (
																<span>
																	{verdi ? verdi.label : adresse.postboksadresse.postnummer}
																</span>
															)}
														</KodeverkConnector>
													)}
												</TitleValue>
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
