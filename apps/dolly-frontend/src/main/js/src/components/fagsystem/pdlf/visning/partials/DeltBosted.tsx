import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UkjentBosted } from '~/components/fagsystem/pdlf/visning/partials/UkjentBosted'

type Data = {
	data: Array<any>
}

type AdresseProps = {
	adresse: any
	idx: number
}

export const Adresse = ({ adresse, idx }: AdresseProps) => {
	return (
		<>
			{adresse.vegadresse && <Vegadresse adresse={adresse} idx={idx} />}
			{adresse.matrikkeladresse && <Matrikkeladresse adresse={adresse} idx={idx} />}
			{adresse.ukjentBosted && <UkjentBosted adresse={adresse} idx={idx} />}
		</>
	)
}

export const DeltBosted = ({ data }: Data) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<SubOverskrift label="Delt bosted" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) => <Adresse adresse={adresse} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
