
function Signup_input({id, placeholder, invalid ,type}) {
    return (
        <div className="input-group mb-3">
            <input type={type} className="form-control needs-validation form-control-lg bg-light fs-6" id={id} placeholder={placeholder} ></input>
            <div className="invalid-feedback">
                {invalid}
            </div>
        </div>

    );
}
export default Signup_input;
