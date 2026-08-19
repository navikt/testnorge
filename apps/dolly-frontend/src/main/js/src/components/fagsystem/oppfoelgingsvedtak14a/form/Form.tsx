import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { oppfoelgingsvedtak14aPath } from '@/components/fagsystem/oppfoelgingsvedtak14a/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import * as React from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { useAlleNavEnheter } from '@/utils/hooks/useNorg2'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const Oppfoelgingsvedtak14aForm = () => {
	const formMethods = useFormContext()
	const { alleNavEnheter, loading: loadingEnheter } = useAlleNavEnheter()

	return (
		<Vis attributt={oppfoelgingsvedtak14aPath}>
			<Panel
				heading="Oppfølgingsvedtak § 14 a"
				hasErrors={usePanelError(oppfoelgingsvedtak14aPath)}
				iconType="arena"
				startOpen={erForsteEllerTest(formMethods.getValues(), [oppfoelgingsvedtak14aPath])}
			>
				<div className={'flexbox--flex-wrap'}>
					<FormSelect
						name={`${oppfoelgingsvedtak14aPath}.innsatsgruppe`}
						label="Innsatsgruppe"
						options={Options('innsatsgruppe')}
						size="xlarge"
						isClearable={false}
					/>
					<FormSelect
						name={`${oppfoelgingsvedtak14aPath}.hovedmal`}
						label="Hovedmål"
						options={Options('hovedmal')}
						isClearable={false}
					/>
					<FormDatepicker
						name={`${oppfoelgingsvedtak14aPath}.vedtakFattet`}
						label="Vedtak fattet"
						minDate={new Date().setHours(0, 0, 0, 0)}
					/>
					<FormSelect
						name={`${oppfoelgingsvedtak14aPath}.oppfolgingsEnhet`}
						label="Oppfølgingsenhet"
						options={alleNavEnheter}
						isLoading={loadingEnheter}
						size="xlarge"
					/>
					<FormTextInput
						name={`${oppfoelgingsvedtak14aPath}.begrunnelse`}
						label="Begrunnelse"
						size="xlarge"
					/>
					<FormTextInput
						name={`${oppfoelgingsvedtak14aPath}.veilederIdent`}
						label="Veileder ident"
					/>
				</div>
			</Panel>
		</Vis>
	)
}

Oppfoelgingsvedtak14aForm.validation = {
	oppfoelgingsvedtak14a: ifPresent(
		'$oppfoelgingsvedtak14a',
		Yup.object({
			innsatsgruppe: requiredString,
			hovedmal: requiredString,
			vedtakFattet: Yup.date().nullable(),
			oppfolgingsEnhet: Yup.string().nullable(),
			begrunnelse: Yup.string().nullable(),
			veilederIdent: Yup.string()
				.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
				.test('len', 'Ident må være et tall med 11 sifre', (val) => !val || val.length === 11)
				.optional()
				.nullable(),
		}),
	),
}
