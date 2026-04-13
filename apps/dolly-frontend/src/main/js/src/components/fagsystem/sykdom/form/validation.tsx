import * as Yup from 'yup'
import { ifPresent, requiredDate } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

export const validation = {
	sykemelding: ifPresent(
		'$sykemelding',
		Yup.object({
			nySykemelding: ifPresent(
				'$sykemelding.nySykemelding',
				Yup.object({
					aktivitet: Yup.array()
						.min(1, 'Må ha minst én aktivitet')
						.of(
							Yup.object({
								grad: Yup.number()
									.nullable()
									.transform((value, original) =>
										original === '' || original === null || original === undefined
											? null
											: value,
									)
									.min(1, 'Grad må være mellom 1 og 99')
									.max(99, 'Grad må være mellom 1 og 99'),
								fom: testDatoFom(requiredDate, 'tom'),
								tom: testDatoTom(requiredDate, 'fom'),
							}),
						),
				}),
			),
		}),
	),
}
