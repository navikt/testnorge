import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '@/components/ui/form/kategori/Kategori'

// @ts-ignore
export const IkkeOppholdSammeVilkaar = () => (
	<div className="flexbox--flex-wrap">
		<Kategori title={'Avslag eller bortfall'}>
			<FormikDatepicker
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avgjorelsesDato'
				}
				label="Avgjørelsesdato"
			/>
		</Kategori>
	</div>
)
