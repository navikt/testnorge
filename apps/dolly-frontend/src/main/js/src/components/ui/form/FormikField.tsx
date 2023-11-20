import { useFormContext } from 'react-hook-form'

const { register } = useFormContext()

export const FormikField = ({ fastfield = true, children, ...props }) => {
	return (
		<input {...props} {...register(props.name)}>
			{children}
		</input>
	)
}
