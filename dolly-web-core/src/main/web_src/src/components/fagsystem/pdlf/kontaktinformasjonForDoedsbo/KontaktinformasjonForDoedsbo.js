import React, { useState } from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Adressat } from './Adressat'
import { Adresse } from './Adresse'
import { Diverse } from './Diverse'

export const KontaktinformasjonForDoedsbo = ({ formikBag }) => {
	return (
		<div>
			<Adressat formikBag={formikBag} />
			<Adresse formikBag={formikBag} />
			<Diverse formikBag={formikBag} />
		</div>
	)
}
