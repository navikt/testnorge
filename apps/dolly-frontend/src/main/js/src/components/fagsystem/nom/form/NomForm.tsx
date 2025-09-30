import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const NomForm = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDatepicker name="nomdata.startDato" label="Startdato" visHvisAvhuket />
			<FormDatepicker name="nomdata.sluttDato" label="Sluttdato" visHvisAvhuket />
		</div>
	)
}
