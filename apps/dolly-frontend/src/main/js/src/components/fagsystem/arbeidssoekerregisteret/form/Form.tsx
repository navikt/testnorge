import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { useFormContext } from 'react-hook-form'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useArbeidssoekerTyper } from '@/utils/hooks/useArbeidssoekerregisteret'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { validation } from '@/components/fagsystem/arbeidssoekerregisteret/form/validation'

export const arbeidssoekerregisteretAttributt = 'arbeidssoekerregisteret'

export const ArbeidssoekerregisteretForm = () => {
	const formMethods = useFormContext()

	const { data: brukertypeOptions, loading: loadingBrukertype } =
		useArbeidssoekerTyper('BRUKERTYPE')

	const { data: nuskodeOptions, loading: loadingNuskode } = useArbeidssoekerTyper('NUSKODE')

	const { data: jobbsituasjonOptions, loading: loadingJobbsituasjon } = useArbeidssoekerTyper(
		'JOBBSITUASJONSBESKRIVELSE',
	)

	const handleStillingChange = (option: any) => {
		formMethods.setValue(
			'arbeidssoekerregisteret.jobbsituasjonsdetaljer.stillingStyrk08',
			option?.value || null,
		)
		formMethods.setValue(
			'arbeidssoekerregisteret.jobbsituasjonsdetaljer.stillingstittel',
			option?.label || null,
		)
	}

	return (
		<Vis attributt={arbeidssoekerregisteretAttributt}>
			<Panel
				heading="Arbeidssøkerregisteret"
				hasErrors={panelError(arbeidssoekerregisteretAttributt)}
				iconType="cv"
				informasjonstekst="Informasjonen i arbeidssøkerregisteret har en levetid på 21 dager fra innsending. Denne kan fornyes ved innsending på ny (gjenopprett)."
				startOpen={erForsteEllerTest(formMethods.getValues(), [arbeidssoekerregisteretAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<FormSelect
						name="arbeidssoekerregisteret.utfoertAv"
						label="Utført av"
						options={brukertypeOptions}
						isLoading={loadingBrukertype}
					/>
					<FormTextInput name="arbeidssoekerregisteret.kilde" label="Kilde" />
					<FormTextInput name="arbeidssoekerregisteret.aarsak" label="Årsak" size="xlarge" />
					<FormSelect
						name="arbeidssoekerregisteret.nuskode"
						label="Utdanningsnivå"
						options={nuskodeOptions}
						isLoading={loadingNuskode}
						size="xlarge"
					/>
					<FormSelect
						name="arbeidssoekerregisteret.jobbsituasjonsbeskrivelse"
						label="Beskrivelse av jobbsituasjonen"
						options={jobbsituasjonOptions}
						isLoading={loadingJobbsituasjon}
						size="large"
					/>
					<div className="flexbox--flex-wrap">
						<FormCheckbox
							name="arbeidssoekerregisteret.utdanningBestaatt"
							label="Utdanning bestått"
							size="small"
						/>
						<FormCheckbox
							name="arbeidssoekerregisteret.utdanningGodkjent"
							label="Utdanning godkjent"
							size="small"
						/>
						<FormCheckbox
							name="arbeidssoekerregisteret.helsetilstandHindrerArbeid"
							label="Helsetilstand hindrer arbeid"
							size="small"
						/>
						<FormCheckbox
							name="arbeidssoekerregisteret.andreForholdHindrerArbeid"
							label="Andre forhold hindrer arbeid"
							size="small"
						/>
					</div>
					<div className="flexbox--full-width">
						<h3>Detaljer om jobbsituasjonen</h3>
					</div>
					<div className="flexbox--flex-wrap">
						<FormDatepicker
							name="arbeidssoekerregisteret.jobbsituasjonsdetaljer.gjelderFraDato"
							label="Gjelder fra dato"
						/>
						<FormDatepicker
							name="arbeidssoekerregisteret.jobbsituasjonsdetaljer.gjelderTilDato"
							label="Gjelder til dato"
						/>
						<FormSelect
							name="arbeidssoekerregisteret.jobbsituasjonsdetaljer.stillingStyrk08"
							label={'Stilling'}
							kodeverk="Yrkesklassifisering"
							value={formMethods.watch(
								'arbeidssoekerregisteret.jobbsituasjonsdetaljer.stillingStyrk08',
							)}
							onChange={handleStillingChange}
							size="xlarge"
						/>
						<FormTextInput
							name="arbeidssoekerregisteret.jobbsituasjonsdetaljer.stillingsprosent"
							label="Stillingsprosent"
							type="number"
						/>
						<FormDatepicker
							name="arbeidssoekerregisteret.jobbsituasjonsdetaljer.sisteDagMedLoenn"
							label="Siste dag med lønn"
						/>
						<FormDatepicker
							name="arbeidssoekerregisteret.jobbsituasjonsdetaljer.sisteArbeidsdag"
							label="Siste arbeidsdag"
						/>
					</div>
				</div>
			</Panel>
		</Vis>
	)
}

ArbeidssoekerregisteretForm.validation = validation
