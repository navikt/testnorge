import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const MedServicebehov = ({ formikBag, path }) => {
	const { arenaforvalter } = formikBag.values

	return (
		<React.Fragment>
			<FormikSelect
				name={`${path}.kvalifiseringsgruppe`}
				label="Servicebehov"
				options={Options('kvalifiseringsgruppe')}
				size="large"
			/>
			{arenaforvalter.aap115 && (
				<Kategori title="11-5-vedtak">
					<FormikDatepicker name={`${path}.aap115[0].fraDato`} label="Fra dato" />
				</Kategori>
			)}

			{arenaforvalter.aap && (
				<Kategori title="AAP-vedtak UA - positivt utfall">
					<FormikDatepicker name={`${path}.aap[0].fraDato`} label="Fra dato" />
					<FormikDatepicker name={`${path}.aap[0].tilDato`} label="Til dato" />
				</Kategori>
			)}

			{arenaforvalter.dagpenger && (
				<Kategori
					hjelpetekst={'Foreløpig er kun ordinære dagpenger støttet'}
					title="Dagpengevedtak"
				>
					<FormikSelect
						name={`${path}.dagpenger[0].rettighetKode`}
						options={Options('rettighetKode')}
						disabled={true}
						value={'DAGO'} // Endre disabled og denne når flere koder blir støttet
						label="Rettighetskode"
						size={'xlarge'}
					/>
					<FormikDatepicker name={`${path}.dagpenger[0].fraDato`} label="Fra dato" />
					<FormikDatepicker name={`${path}.dagpenger[0].tilDato`} label="Til dato" />
					<FormikDatepicker name={`${path}.dagpenger[0].mottattDato`} label="Mottatt dato" />
				</Kategori>
			)}
		</React.Fragment>
	)
}
