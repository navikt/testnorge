import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { oppfoelgingsvedtak14aPath } from '@/components/fagsystem/oppfoelgingsvedtak14a/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import * as React from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { useAlleNavEnheter } from '@/utils/hooks/useNorg2'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { useKodeverkOppfoelgingsvedtak14a } from '@/utils/hooks/useOppfoelgingsvedtak14a'
import { useEffect, useState } from 'react'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import * as _ from 'lodash-es'

export const Oppfoelgingsvedtak14aForm = () => {
	const formMethods = useFormContext()
	const { alleNavEnheter, loading: loadingEnheter } = useAlleNavEnheter()

	const [randomNavUsers, setRandomNavUsers] = useState<{ value: string; label: string }[]>([])
	useEffect(() => {
		setRandomNavUsers(genererTilfeldigeNavPersonidenter() as { value: string; label: string }[])
	}, [])

	const veileder = formMethods.watch(`${oppfoelgingsvedtak14aPath}.veilederIdent`)

	const { options: innsatsgruppeOptions, loading: innsatsgruppeLoading } =
		useKodeverkOppfoelgingsvedtak14a('innsatsgruppe')

	const { options: hovedmalOptions, loading: hovedmalLoading } =
		useKodeverkOppfoelgingsvedtak14a('hovedmal')

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
						options={innsatsgruppeOptions}
						isLoading={innsatsgruppeLoading}
						size="xlarge"
						isClearable={false}
					/>
					<FormSelect
						name={`${oppfoelgingsvedtak14aPath}.hovedmal`}
						label="Hovedmål"
						options={hovedmalOptions}
						isLoading={hovedmalLoading}
						isClearable={false}
					/>
					<FormDatepicker
						name={`${oppfoelgingsvedtak14aPath}.vedtakFattet`}
						label="Vedtak fattet"
						minDate={new Date(new Date().setHours(0, 0, 0, 0))}
					/>
					<FormSelect
						name={`${oppfoelgingsvedtak14aPath}.oppfolgingsEnhet`}
						label="Oppfølgingsenhet"
						options={alleNavEnheter}
						isLoading={loadingEnheter}
						size="xlarge"
						info="Hvis ikke valgt, vil oppfølgingsenhet automatisk settes til Nav-kontor tilhørende brukers adresse."
					/>
					<FormTextInput
						name={`${oppfoelgingsvedtak14aPath}.begrunnelse`}
						label="Begrunnelse"
						size="xlarge"
					/>
					<FormSelect
						name={`${oppfoelgingsvedtak14aPath}.veilederIdent`}
						label="Veileder ident"
						options={
							_.isEmpty(veileder)
								? randomNavUsers
								: [...randomNavUsers, { value: veileder, label: veileder }]
						}
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
			veilederIdent: Yup.string().nullable(),
		}),
	),
}
