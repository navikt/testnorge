import { getInitialKjoeretoey } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export const KjoeretoeyArrayForm = ({ path }) => (
	<FormDollyFieldArray
		name={path}
		header="Spesifisering"
		newEntry={getInitialKjoeretoey()}
		canBeEmpty={true}
		whiteBackground={true}
		nested
	>
		{(kjoeretoeyPath, idx) => (
			<div className="flexbox--flex-wrap sigrun-form" key={idx}>
				<FormSelect
					name={`${kjoeretoeyPath}.type`}
					size={'large'}
					label="Type"
					value={'Kjoeretoey'}
					isDisabled={true}
					options={[
						// { value: 'EiendelerOgFasteEiendommer', label: 'Eiendeler og faste eiendommer' },
						{ value: 'Kjoeretoey', label: 'Kjøretøy' }, //Kun kjøretøy som er støttet foreløpig
						// { value: 'Spesifisering', label: 'Spesifisering' },
					]}
				/>
				<div style={{ marginLeft: '-25px', marginRight: '20px', paddingTop: '30px' }}>
					<Hjelpetekst> Foreløpig er det kun støtte for kjøretøy. </Hjelpetekst>
				</div>
				<FormTextInput
					name={`${kjoeretoeyPath}.aarForFoerstegangsregistrering`}
					label="År for førstegangsreg."
					type={'number'}
				/>
				<FormTextInput
					name={`${kjoeretoeyPath}.antattMarkedsverdi`}
					label="Antatt markedsverdi"
					type="number"
				/>
				<FormTextInput
					name={`${kjoeretoeyPath}.antattVerdiSomNytt`}
					label="Antatt verdi som nytt"
					type="number"
				/>
				<FormTextInput name={`${kjoeretoeyPath}.beloep`} label="Beløp" type={'number'} />
				<FormTextInput
					name={`${kjoeretoeyPath}.eierandel`}
					label="Eierandel beløp"
					type={'number'}
				/>
				<FormTextInput name={`${kjoeretoeyPath}.fabrikatnavn`} label="Fabrikatnavn" />
				<FormTextInput
					name={`${kjoeretoeyPath}.formuesverdi`}
					label="Formuesverdi"
					type={'number'}
				/>
				<FormTextInput
					name={`${kjoeretoeyPath}.formuesverdiForFormuesandel`}
					label="Formuesverdi for formuesandel"
					type={'number'}
				/>
				<FormTextInput name={`${kjoeretoeyPath}.registreringsnummer`} label="Registreringsnummer" />
			</div>
		)}
	</FormDollyFieldArray>
)
