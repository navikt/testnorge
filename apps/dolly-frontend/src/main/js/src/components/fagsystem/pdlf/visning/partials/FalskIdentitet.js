import React, { Fragment } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Personnavn } from './Personnavn'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

export const FalskIdentitet = ({ data }) => {
	if (!data) return null
	const erListe = Array.isArray(data)

	const FalskIdentVisning = ({ enhet, id }) => {
		const {
			rettIdentitetVedOpplysninger,
			rettIdentitetErUkjent,
			rettIdentitetVedIdentifikasjonsnummer,
		} = enhet

		return (
			<div className="person-visning_content" key={id}>
				{rettIdentitetErUkjent && <TitleValue title="Rett identitet" value={'UKJENT'} />}

				<TitleValue title="Rett fnr/dnr/npid" value={rettIdentitetVedIdentifikasjonsnummer} />

				{rettIdentitetVedOpplysninger && (
					<Fragment>
						<TitleValue title="Rett identitet" value={'Kjent ved personopplysninger'} />

						<Personnavn data={rettIdentitetVedOpplysninger.personnavn} />

						<TitleValue
							title="Fødselsdato"
							value={Formatters.formatDate(rettIdentitetVedOpplysninger.foedselsdato)}
						/>

						<TitleValue title="Kjønn" value={rettIdentitetVedOpplysninger.kjoenn} />

						<TitleValue
							title="Statsborgerskap"
							value={
								rettIdentitetVedOpplysninger.statsborgerskap &&
								rettIdentitetVedOpplysninger.statsborgerskap.join(', ')
							}
						/>
					</Fragment>
				)}
			</div>
		)
	}

	return (
		<div>
			<SubOverskrift label="Falsk identitet" iconKind="identifikasjon" />
			<div className="person-visning_content">
				<ErrorBoundary>
					{erListe ? (
						<DollyFieldArray data={data} header="" nested>
							{(enhet, idx) => <FalskIdentVisning enhet={enhet} id={idx} />}
						</DollyFieldArray>
					) : (
						<FalskIdentVisning enhet={data} id={0} />
					)}
				</ErrorBoundary>
			</div>
		</div>
	)
}
