import React, { useContext } from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

export const MedServicebehov = ({ formikBag }) => {
	const { arenaforvalter } = formikBag.values

	const opts = useContext(BestillingsveilederContext)
	const { personFoerLeggTil } = opts

	const uregistert = !(personFoerLeggTil && personFoerLeggTil.arenaforvalteren)

	return (
		<React.Fragment>
			{uregistert && (
				<FormikSelect
					name="arenaforvalter.kvalifiseringsgruppe"
					label="Servicebehov"
					options={Options('kvalifiseringsgruppe')}
					size="large"
				/>
			)}
			{arenaforvalter.aap115 && (
				<Kategori title="11-5-vedtak">
					<FormikDatepicker name="arenaforvalter.aap115[0].fraDato" label="Fra dato" />
				</Kategori>
			)}

			{arenaforvalter.aap && (
				<Kategori title="AAP-vedtak UA - positivt utfall">
					<FormikDatepicker name="arenaforvalter.aap[0].fraDato" label="Fra dato" />
					<FormikDatepicker name="arenaforvalter.aap[0].tilDato" label="Til dato" />
				</Kategori>
			)}

			{arenaforvalter.dagpenger && (
				<Kategori
					hjelpetekst={'Foreløpig er kun ordinære dagpenger støttet'}
					title="Dagpengevedtak"
				>
					<FormikSelect
						name="arenaforvalter.dagpenger[0].rettighetKode"
						options={Options('rettighetKode')}
						disabled={true}
						value={'DAGO'} // Endre disabled og denne når flere koder blir støttet
						label="Rettighetskode"
						size={'xlarge'}
					/>
					<FormikDatepicker name="arenaforvalter.dagpenger[0].fraDato" label="Fra dato" />
					<FormikDatepicker name="arenaforvalter.dagpenger[0].tilDato" label="Til dato" />
					<FormikDatepicker name="arenaforvalter.dagpenger[0].mottattDato" label="Mottatt dato" />
				</Kategori>
			)}
		</React.Fragment>
	)
}
