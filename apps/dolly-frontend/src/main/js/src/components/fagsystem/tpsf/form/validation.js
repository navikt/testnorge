import * as Yup from 'yup'
import _get from 'lodash/get'
import { addDays, isAfter, isBefore } from 'date-fns'
import Dataformatter from '~/utils/DataFormatter'
import { messages } from '~/utils/YupValidations'

export const sivilstander = Yup.array().of(
	Yup.object({
		sivilstand: Yup.string().required(messages.required),
		sivilstandRegdato: Yup.date()
			.transform((i, j) => (j === null || j === '' ? undefined : i))
			.test(
				'is-after-last',
				'Dato må være etter tidligere forhold (eldste forhold settes først)',
				function validDate(dato) {
					if (!dato) return true
					const values = this.options.context
					const path = this.options.path

					// Finn index av current partner og sivilstand
					// Ex path: tpsf.relasjoner.partnere[0].sivilstander[0].sivilstandRegdato
					const partnerIdx = parseInt(path.match(/partnere\[(.*?)\].sivilstander/i)[1])
					const sivilstandIdx = parseInt(path.match(/sivilstander\[(.*?)\].sivilstandRegdato/i)[1])

					// Da vi skal validere mot "forrige forhold" trenger vi ikke sjekke første
					if (partnerIdx === 0 && sivilstandIdx === 0) return true

					const getSisteForholdDato = () => {
						if (!values.personFoerLeggTil?.tpsf?.sivilstander) {
							return null
						}
						const datoer = values.personFoerLeggTil.tpsf.sivilstander
							.filter((sivstand) => sivstand.sivilstandRegdato)
							.map((sivilstand) => new Date(sivilstand.sivilstandRegdato))
							.sort((a, b) => b.getTime() - a.getTime()) // Seneste dato på første index
						return datoer && datoer.length > 0 && datoer[0].toISOString().substring(0, 10)
					}

					const getSivilstandRegdato = (pIdx, sIdx) =>
						_get(
							values.tpsf.relasjoner.partnere,
							`[${pIdx}].sivilstander[${sIdx}].sivilstandRegdato`
						)

					let prevDato

					if (sivilstandIdx > 0) {
						prevDato = getSivilstandRegdato(partnerIdx, sivilstandIdx - 1)
					} else {
						const prevPartnerSivilstandArr = _get(
							values.tpsf.relasjoner.partnere,
							`[${partnerIdx - 1}].sivilstander`
						)
						prevDato = getSivilstandRegdato(partnerIdx - 1, prevPartnerSivilstandArr.length - 1)
					}
					if (!prevDato) {
						prevDato = getSisteForholdDato()
					}

					// Selve testen
					const dateValidAfter = isAfter(new Date(dato), addDays(new Date(prevDato), 2))
					const dateValidBeforeToday = isBefore(new Date(dato), Date.now())
					return (
						(dateValidAfter && dateValidBeforeToday) ||
						this.createError({
							message: !dateValidAfter
								? `Dato må være etter tidligere forhold (${Dataformatter.formatDate(
										prevDato
								  )}), og det må minst være 2 dager i mellom`
								: 'Dato kan ikke være etter dagens dato',
							path: path,
						})
					)
				}
			)
			.required(messages.required),
	})
)
