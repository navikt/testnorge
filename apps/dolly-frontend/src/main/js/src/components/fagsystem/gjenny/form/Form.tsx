import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { etterlatteYtelserAttributt } from '@/components/bestillingsveileder/stegVelger/steg/steg1/paneler/EtterlatteYtelser'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { usePdlMiljoeinfo } from '@/utils/hooks/usePdlPerson'

export const initialEtterlatteYtelser = {
	ytelse: null,
	soeker: null,
}

export const EtterlatteYtelserForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext)

	const gyldigeSivilstander = [
		'GIFT',
		'SAMBOER',
		'REGISTRERT_PARTNER',
		'SEPARERT',
		'SEPARERT_PARTNER',
	]

	const doedsfall =
		formMethods.watch('pdldata.person.doedsfall') ||
		opts?.personFoerLeggTil?.pdlforvalter?.person?.doedsfall ||
		opts?.personFoerLeggTil?.pdl?.hentPerson?.doedsfall ||
		opts?.importPersoner?.flatMap((person) => person.data?.hentPerson?.doedsfall)

	const sivilstand =
		formMethods.watch('pdldata.person.sivilstand') ||
		opts?.personFoerLeggTil?.pdlforvalter?.person?.sivilstand ||
		opts?.personFoerLeggTil?.pdl?.hentPerson?.sivilstand ||
		opts?.importPersoner?.flatMap((person) => person.data?.hentPerson?.sivilstand)

	const partner = sivilstand?.find((sivilstand) => gyldigeSivilstander.includes(sivilstand.type))

	const { pdlData: partnerInfo } = usePdlMiljoeinfo(partner?.relatertVedSivilstand)

	const partnerDoedsfall = partnerInfo?.hentPerson?.doedsfall

	const barn =
		formMethods
			.watch('pdldata.person.forelderBarnRelasjon')
			?.filter((item) => item.relatertPersonsRolle === 'BARN') ||
		opts?.personFoerLeggTil?.pdlforvalter?.person?.relasjoner?.filter(
			(item) => item.relasjonType === 'FAMILIERELASJON_BARN',
		) ||
		opts?.personFoerLeggTil?.pdl?.hentPerson?.forelderBarnRelasjon?.filter(
			(item) => item.relatertPersonsRolle === 'BARN',
		) ||
		opts?.importPersoner?.flatMap((person) =>
			person.data?.hentPerson?.forelderBarnRelasjon?.filter(
				(item) => item.relatertPersonsRolle === 'BARN',
			),
		)
	// console.log('barn: ', barn) //TODO - SLETT MEG

	const getInfotekst = (ytelse) => {
		if (ytelse === 'OMSTILLINGSSTOENAD') {
			if (!partner) {
				return 'For å få omstillingsstønad må hovedperson ha en partner, og enten hovedperson eller partner må være død.'
			}
			if (
				(!doedsfall || doedsfall?.length < 1) &&
				(!partnerDoedsfall || partnerDoedsfall?.length < 1)
			) {
				return 'For å få omstillingsstønad må hovedperson eller partner være død.'
			}
		} else if (ytelse === 'BARNEPENSJON') {
			if (!barn || barn?.length < 1) {
				return 'For å få barnepensjon må hovedperson ha barn, og enten hovedperson eller evt. partner må være død.'
			}
			if (
				(!doedsfall || doedsfall?.length < 1) &&
				(!partnerDoedsfall || partnerDoedsfall?.length < 1)
			) {
				return 'For å få barnepensjon må hovedperson eller evt. partner være død.'
			}
		}
		return null
	}

	return (
		<Vis attributt={etterlatteYtelserAttributt}>
			<Panel
				heading="Etterlatteytelser"
				hasErrors={panelError(etterlatteYtelserAttributt)}
				iconType="grav"
				startOpen={erForsteEllerTest(formMethods.getValues(), [etterlatteYtelserAttributt])}
			>
				<ErrorBoundary>
					<FormDollyFieldArray
						name="etterlatteYtelser"
						header="Etterlatteytelse"
						newEntry={initialEtterlatteYtelser}
						canBeEmpty={false}
					>
						{(path: string) => {
							const ytelse = formMethods.watch(`${path}.ytelse`)
							const infotekst = getInfotekst(ytelse)
							return (
								<>
									{infotekst && (
										<StyledAlert variant="warning" size="small" style={{ width: '100%' }}>
											{infotekst}
										</StyledAlert>
									)}
									<FormSelect
										name={`${path}.ytelse`}
										label="Ytelse"
										options={Options('etterlatteYtelser')}
										size="large"
										isClearable={false}
									/>
									{/*//TODO: Ident for barn hvis flere barn. Kanskje bare vises ved legg til/endre og import?*/}
									<FormSelect
										name={`${path}.soeker`}
										label="Søker"
										options={null}
										size="xxlarge"
										isDisabled={true}
									/>
								</>
							)
						}}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

EtterlatteYtelserForm.validation = {
	etterlatteYtelser: ifPresent(
		'$etterlatteYtelser',
		Yup.array().of(
			Yup.object({
				ytelse: requiredString,
				soeker: Yup.string().nullable(),
			}),
		),
	),
}
